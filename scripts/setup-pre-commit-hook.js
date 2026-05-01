#!/usr/bin/env node

/**
 * Setup pre-commit hook to auto-update AGENTS.md
 * Run: node scripts/setup-pre-commit-hook.js
 */

const fs = require('fs');
const path = require('path');
const os = require('os');

const PROJECT_ROOT = path.resolve(__dirname, '..');
const GIT_DIR = path.join(PROJECT_ROOT, '.git');
const HOOKS_DIR = path.join(GIT_DIR, 'hooks');
const PRE_COMMIT_HOOK = path.join(HOOKS_DIR, 'pre-commit');

const HOOK_CONTENT = `#!/bin/bash

# Auto-update AGENTS.md on commit if project structure changed
cd "$(git rev-parse --show-toplevel)"

echo "🔄 Checking if AGENTS.md needs update..."

node scripts/update-agents-md.js > /dev/null 2>&1

if git diff --cached AGENTS.md > /dev/null; then
    echo "✅ AGENTS.md auto-updated. Staging changes..."
    git add AGENTS.md
fi
`;

function setup() {
  console.log('Setting up pre-commit hook for AGENTS.md auto-update...\n');

  if (!fs.existsSync(GIT_DIR)) {
    console.error('❌ .git directory not found. Is this a git repository?');
    process.exit(1);
  }

  if (!fs.existsSync(HOOKS_DIR)) {
    console.log('📁 Creating .git/hooks directory...');
    fs.mkdirSync(HOOKS_DIR, { recursive: true });
  }

  if (fs.existsSync(PRE_COMMIT_HOOK)) {
    const existing = fs.readFileSync(PRE_COMMIT_HOOK, 'utf8');
    if (existing.includes('AGENTS.md')) {
      console.log('ℹ️  Pre-commit hook already configured.');
      process.exit(0);
    }
    console.log('⚠️  Existing pre-commit hook found. Backing up to pre-commit.bak');
    fs.copyFileSync(PRE_COMMIT_HOOK, PRE_COMMIT_HOOK + '.bak');
  }

  fs.writeFileSync(PRE_COMMIT_HOOK, HOOK_CONTENT, 'utf8');

  // Make executable on Unix
  if (os.platform() !== 'win32') {
    fs.chmodSync(PRE_COMMIT_HOOK, '755');
    console.log('✅ Pre-commit hook installed and made executable\n');
  } else {
    console.log('✅ Pre-commit hook installed\n');
    console.log('⚠️  On Windows with Git Bash, the hook should work automatically.');
    console.log('   If using Git for Windows native, consider using husky or another hook manager.\n');
  }

  console.log('📝 Hook location:', PRE_COMMIT_HOOK);
  console.log('\n✨ Setup complete! AGENTS.md will auto-update on next commit.');
}

setup();

