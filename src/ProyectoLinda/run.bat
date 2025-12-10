@echo off
echo ==========================================
echo   1. LIMPIANDO ARCHIVOS ANTIGUOS (.class)
echo ==========================================

:: El comando del /s /q borra recursivamente todos los .class
:: para asegurar que no quedan versiones viejas (Rebuild real)
del /s /q *.class

echo.
echo ==========================================
echo   COMPILANDO EL SISTEMA LINDA...
echo ==========================================

:: Cambiar al directorio padre (fuera de ProyectoLinda)
cd ..

:: 1. Compilamos todo junto para evitar errores de dependencias
javac ProyectoLinda/comun/*.java ProyectoLinda/server/*.java ProyectoLinda/proxy/*.java ProyectoLinda/cliente/*.java

if %errorlevel% neq 0 (
    echo Error de compilacion. Revisa el codigo.
    pause
    exit /b
)

echo.
echo ==========================================
echo   LANZANDO NODOS DE ALMACENAMIENTO
echo ==========================================

:: Nodo A (Puerto 8001 - Tuplas cortas 1-3)
start "NODO A (8001)" cmd /k java ProyectoLinda.server.ServidorNodo 8001

:: Nodo B (Puerto 8002 - Tuplas medias 4-5)
start "NODO B (8002)" cmd /k java ProyectoLinda.server.ServidorNodo 8002

:: Nodo C (Puerto 8003 - Tuplas largas 6)
start "NODO C (8003)" cmd /k java ProyectoLinda.server.ServidorNodo 8003

:: Nodo Replica (Puerto 8004 - Respaldo de A)
start "REPLICA A (8004)" cmd /k java ProyectoLinda.server.ServidorNodo 8004

echo Esperando a que los nodos arranquen...
timeout /t 2 >nul

echo.
echo ==========================================
echo   LANZANDO PROXY CENTRAL
echo ==========================================

:: Servidor Central (Puerto 8000)
start "PROXY LINDA (8000)" cmd /k java ProyectoLinda.proxy.ServidorCentralLinda

echo Esperando al proxy...
timeout /t 2 >nul

echo.
echo ==========================================
echo   LANZANDO CLIENTE
echo ==========================================

:: Cliente de usuario
start "CLIENTE LINDA" cmd /k java ProyectoLinda.cliente.AplicacionCliente

echo SISTEMA INICIADO.
