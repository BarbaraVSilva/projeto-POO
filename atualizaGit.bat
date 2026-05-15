@echo off
setlocal
echo === Iniciando Atualizacao do Git: %CD% ===
git branch -m main 2>nul
git add .
set datetime=%date% %time%
git commit -m "Backup Automatico: %datetime%"
git pull origin main --rebase -X ours 2>nul
git push origin main --force 2>nul
echo === Atualizacao concluida em %CD% ===
timeout /t 2 >nul
endlocal
