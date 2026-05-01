#!/usr/bin/env node

/**
 * Auto-update AGENTS.md with current project information
 * Run: node scripts/update-agents-md.js
 *
 * This script scans the codebase and updates:
 * - Domain list (from Backend/Oenobox/src/main/java/lu/caepatriot/oenobox)
 * - API endpoints (from controller files)
 * - Frontend modules (from Frontend/Frontend/src/modules)
 * - Flyway migrations (from migration folder)
 * - Dependencies (from pom.xml and package.json)
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const PROJECT_ROOT = path.resolve(__dirname, '..');
const AGENTS_FILE = path.join(PROJECT_ROOT, 'AGENTS.md');
const BACKEND_SRC = path.join(PROJECT_ROOT, 'Backend/Oenobox/src/main/java/lu/caepatriot/oenobox');
const FRONTEND_MODULES = path.join(PROJECT_ROOT, 'Frontend/Frontend/src/modules');
const MIGRATIONS_DIR = path.join(PROJECT_ROOT, 'Backend/Oenobox/src/main/resources/db/migration');

function getDomains() {
  try {
    if (!fs.existsSync(BACKEND_SRC)) return [];
    return fs.readdirSync(BACKEND_SRC)
      .filter(f => fs.statSync(path.join(BACKEND_SRC, f)).isDirectory())
      .filter(f => f !== 'common')
      .sort();
  } catch (e) {
    console.warn('⚠️  Could not read domains:', e.message);
    return [];
  }
}

function getFrontendModules() {
  try {
    if (!fs.existsSync(FRONTEND_MODULES)) return [];
    return fs.readdirSync(FRONTEND_MODULES)
      .filter(f => fs.statSync(path.join(FRONTEND_MODULES, f)).isDirectory())
      .sort();
  } catch (e) {
    console.warn('⚠️  Could not read frontend modules:', e.message);
    return [];
  }
}

function getMigrations() {
  try {
    if (!fs.existsSync(MIGRATIONS_DIR)) return [];
    return fs.readdirSync(MIGRATIONS_DIR)
      .filter(f => f.startsWith('V') && f.endsWith('.sql'))
      .sort((a, b) => {
        const numA = parseInt(a.match(/\d+/)[0]);
        const numB = parseInt(b.match(/\d+/)[0]);
        return numA - numB;
      });
  } catch (e) {
    console.warn('⚠️  Could not read migrations:', e.message);
    return [];
  }
}

function getSpringBootVersion() {
  try {
    const pomPath = path.join(PROJECT_ROOT, 'Backend/Oenobox/pom.xml');
    if (!fs.existsSync(pomPath)) return 'unknown';
    const content = fs.readFileSync(pomPath, 'utf8');
    const match = content.match(/<version>([^<]+)<\/version>/);
    return match ? match[1] : 'unknown';
  } catch (e) {
    return 'unknown';
  }
}

function getVueVersion() {
  try {
    const packagePath = path.join(PROJECT_ROOT, 'Frontend/Frontend/package.json');
    if (!fs.existsSync(packagePath)) return 'unknown';
    const pkg = JSON.parse(fs.readFileSync(packagePath, 'utf8'));
    return pkg.dependencies.vue || 'unknown';
  } catch (e) {
    return 'unknown';
  }
}

function updateLastUpdated(content) {
  const today = new Date();
  const months = ['January', 'February', 'March', 'April', 'May', 'June',
                  'July', 'August', 'September', 'October', 'November', 'December'];
  const dateStr = `${months[today.getMonth()]} ${today.getDate()}, ${today.getFullYear()}`;

  return content.replace(
    /\*\*Last Updated:\*\* [^\n]+/,
    `**Last Updated:** ${dateStr}`
  );
}

function generateDomainSection(domains) {
  if (domains.length === 0) return null;

  const domainLines = domains.map(d => `│   │   ├── ${d}/            # Domain: ${d}`).join('\n');
  return domainLines;
}

function updateArchitectureSection(content, domains, frontendModules) {
  if (domains.length === 0) return content;

  const backendDomainsStr = domains.map(d => `│   │   ├── ${d}/`).join('\n');
  const frontendModulesStr = frontendModules.map(m => `│       ├── ${m}/`).join('\n');

  // Update Backend domains list
  const backendPattern = /│   │   ├── cellar\/[\s\S]*?│   │   └── common\//;
  const backendReplacement = `${backendDomainsStr}
│   │   └── common/`;

  if (backendPattern.test(content)) {
    content = content.replace(backendPattern, backendReplacement);
  }

  // Update Frontend modules list
  const frontendPattern = /│       ├── modules\/[\s\S]*?│       ├── core\//;
  const frontendReplacement = `│       ├── modules/
${frontendModulesStr}
│       ├── core/`;

  if (frontendPattern.test(content)) {
    content = content.replace(frontendPattern, frontendReplacement);
  }

  return content;
}

function updateMigrationsSection(content, migrations) {
  if (migrations.length === 0) return content;

  const migrationLines = migrations.slice(-5).map(m => {
    const desc = m.replace(/V\d+__/, '').replace(/.sql/, '').replace(/_/g, ' ');
    return `- \`${m}\` — ${desc}`;
  }).join('\n');

  const pattern = /- `V1__init_schema\.sql`[\s\S]*?- `V13__inventory_intake_foundation\.sql`/;
  const replacement = migrations.slice(-5).map(m => {
    const desc = m.replace(/V\d+__/, '').replace(/.sql/, '').replace(/_/g, ' ');
    return `- \`${m}\` — ${desc}`;
  }).join('\n');

  if (pattern.test(content)) {
    content = content.replace(pattern, replacement);
  }

  return content;
}

function updateVersionsSection(content) {
  const springVersion = getSpringBootVersion();
  const vueVersion = getVueVersion();

  content = content.replace(
    /Spring Boot 3\.4\.0/,
    `Spring Boot ${springVersion}`
  );

  content = content.replace(
    /Vue 3/,
    `Vue ${vueVersion}`
  );

  return content;
}

function main() {
  console.log('📝 Updating AGENTS.md with current project information...\n');

  if (!fs.existsSync(AGENTS_FILE)) {
    console.error('❌ AGENTS.md not found at', AGENTS_FILE);
    process.exit(1);
  }

  let content = fs.readFileSync(AGENTS_FILE, 'utf8');
  const originalContent = content;

  // Gather current project state
  const domains = getDomains();
  const frontendModules = getFrontendModules();
  const migrations = getMigrations();

  console.log('✅ Found domains:', domains.join(', '));
  console.log('✅ Found frontend modules:', frontendModules.join(', '));
  console.log('✅ Found', migrations.length, 'migrations');

  // Update sections
  content = updateArchitectureSection(content, domains, frontendModules);
  content = updateMigrationsSection(content, migrations);
  content = updateVersionsSection(content);
  content = updateLastUpdated(content);

  // Write back
  fs.writeFileSync(AGENTS_FILE, content, 'utf8');

  if (content !== originalContent) {
    console.log('\n✨ AGENTS.md updated successfully!');
  } else {
    console.log('\n ℹ️  No changes needed.');
  }
}

main();

