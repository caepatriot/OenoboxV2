# Scripts Documentation

Automation scripts to keep project documentation up to date.

## Update AGENTS.md Script

**File:** `update-agents-md.js`

Automatically scans the codebase and updates AGENTS.md with current information:

### What it Updates
- Backend domains from `Backend/Oenobox/src/main/java/lu/caepatriot/oenobox/`
- Frontend modules from `Frontend/Frontend/src/modules/`
- Flyway migrations from `Backend/Oenobox/src/main/resources/db/migration/`
- Spring Boot version
- Vue version
- Last updated timestamp

### Usage

#### Manual Run
```powershell
node scripts/update-agents-md.js
```

#### After Adding Features
Run this after:
- Creating new domain (`cellar/`, `inventory/`, `tasting/`, `winecatalog/`)
- Adding frontend module (`modules/newfeature/`)
- Creating database migration
- Upgrading Spring Boot or Vue

#### In Build Pipelines
Add to CI/CD before publishing documentation:
```yaml
- name: Update AGENTS.md
  run: node scripts/update-agents-md.js
```

## Setup Pre-Commit Hook

**File:** `setup-pre-commit-hook.js`

Configures Git to auto-update AGENTS.md before each commit.

### Usage
```powershell
node scripts/setup-pre-commit-hook.js
```

### What It Does
- Creates `.git/hooks/pre-commit` hook
- Auto-runs `update-agents-md.js` on every commit
- Automatically stages AGENTS.md changes if modified
- Backs up existing hooks to `.git/hooks/pre-commit.bak`

### Notes
- **One-time setup**: Install once per repository clone
- **Windows users**: Works with Git Bash. For native cmd.exe, consider using **husky**
- **Safe**: Only updates if project structure changed

## GitHub Actions Workflow

**File:** `.github/workflows/update-agents-md.yml`

Automatic updates on GitHub via CI/CD pipeline.

### Triggers
Runs when these paths change:
- `Backend/Oenobox/src/main/java/lu/caepatriot/oenobox/*` — Backend domains
- `Backend/Oenobox/pom.xml` — Spring dependencies
- `Frontend/Frontend/src/modules/*` — Frontend modules
- `Frontend/Frontend/package.json` — Frontend dependencies
- `Backend/Oenobox/src/main/resources/db/migration/*` — Database schema

### Branches
- `main`
- `develop`

### Automatic Commit
If changes are detected, the workflow:
1. Runs the update script
2. Commits changes as `chore: auto-update AGENTS.md with current project structure`
3. Pushes back to the branch

## Recommended Setup

### For Local Development
```powershell
# One-time setup
node scripts/setup-pre-commit-hook.js

# Then just commit normally - AGENTS.md auto-updates!
```

### For CI/CD Integration
The GitHub Actions workflow handles this automatically.

### For Code Review
Reviewers can manually verify AGENTS.md accuracy:
```powershell
node scripts/update-agents-md.js
# Check if any changes occurred - indicates drift from documentation
```

## Troubleshooting

### Script can't find project files
- Ensure you're running from project root
- Check that paths exist: `Backend/Oenobox/src/main/java/lu/caepatriot/oenobox/`

### Pre-commit hook not running
On Windows with Git for Windows (not Git Bash):
- Option 1: Install and use **husky**: `npm install husky --save-dev && npx husky install`
- Option 2: Use **Windows Subsystem for Linux (WSL)** with Git Bash
- Option 3: Manually run before commits: `node scripts/update-agents-md.js`

### GitHub Actions not triggering
- Check branch name matches workflow (main or develop)
- Verify file paths in `on.push.paths` match your structure
- Check GitHub Actions is enabled in repository settings

---

For questions or issues, refer to **AGENTS.md → Maintaining This Document** section.

