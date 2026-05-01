# AGENTS.md Auto-Update Setup Guide

## Choose Your Integration Level

### Option 1: Manual Updates (Most Flexibility)
Run script after making structural changes:
```powershell
node scripts/update-agents-md.js
```

### Option 2: Automatic on Every Commit (Recommended)
One-time setup with pre-commit hook:
```powershell
node scripts/setup-pre-commit-hook.js
```
Then commit normally — AGENTS.md auto-updates in background.

### Option 3: CI/CD Automation (Enterprise)
Already included: `.github/workflows/update-agents-md.yml`
- Automatically triggers on code push
- Updates AGENTS.md on main/develop branches
- Auto-commits changes

---

## Quick Start

### First Time Setup
```powershell
# From project root
node scripts/setup-pre-commit-hook.js
```

### Then Just Code
```powershell
# Create new domain
mkdir "Backend/Oenobox/src/main/java/lu/caepatriot/oenobox/reporting"

# Commit normally
git add .
git commit -m "feat: add reporting domain"

# ✨ AGENTS.md auto-updates!
```

### What Gets Updated Automatically

| Change | Auto-updated |
|--------|-------------|
| New backend domain | ✅ Yes |
| New frontend module | ✅ Yes |
| New migration | ✅ Yes |
| Updated dependencies | ✅ Yes |
| API endpoint changes | ⚠️ Manual review needed |
| Service patterns | ⚠️ Manual review needed |
| Documentation fixes | ❌ No |

---

## Troubleshooting

### On Windows
If pre-commit hook doesn't run with cmd.exe:
1. Use **Git Bash** instead
2. Or use **husky**: `npm install husky && npx husky install`
3. Or run manually: `node scripts/update-agents-md.js`

### Script reports "unknown"
- Ensure `pom.xml` exists in `Backend/Oenobox/`
- Ensure `package.json` exists in `Frontend/Frontend/`
- Re-run after fixing file locations

### GitHub Actions not triggering
- Check workflows enabled in repository settings
- Verify branch is `main` or `develop`
- Check file paths match your structure

---

## For AI Agents

**AGENTS.md is now self-healing!** 

When you:
- Add a new backend domain
- Create frontend modules
- Run database migrations
- Update dependencies

Just commit and push. AGENTS.md will automatically update with:
- New domain documentation
- Updated version numbers
- Latest migration history
- Current stack information

You always have the **latest, accurate** project information to reference.

---

See `scripts/README.md` for detailed documentation.

