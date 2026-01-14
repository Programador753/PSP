@echo off
REM ================================================
REM Script de Prueba Completa del Sistema
REM ================================================

echo ================================================
echo PRUEBA DE SINCRONIZACION CON SEMAFORO
echo Sistema Linda - Recuperacion Automatica
echo ================================================
echo.
echo Este script demuestra:
echo   1. Servidor REPLICA (8004) inicia y recibe datos
echo   2. Servidor PRINCIPAL (8001) se cae/apaga
echo   3. Clientes escriben en REPLICA mientras PRINCIPAL esta caido
echo   4. PRINCIPAL se reinicia y RECUPERA todos los datos
echo   5. Durante la sincronizacion, escrituras estan BLOQUEADAS
echo.
pause

REM Paso 1: Iniciar Replica
echo.
echo ================================================
echo [1/5] Iniciando servidor REPLICA en puerto 8004
echo ================================================
start "REPLICA (8004)" cmd /k "cd C:\Users\anton\IdeaProjects\psp\src && echo Servidor REPLICA iniciado en puerto 8004 && java ProyectoLinda.server.ServidorNodo 8004"
timeout /t 3

REM Paso 2: Enviar datos a la replica
echo.
echo ================================================
echo [2/5] Enviando 10 mensajes al servidor REPLICA
echo ================================================
timeout /t 2
start "Cliente Replica" cmd /k "cd C:\Users\anton\IdeaProjects\psp\src && java ProyectoLinda.cliente.ClientePrueba localhost 8004 10"
timeout /t 5

echo.
echo ================================================
echo [3/5] Datos escritos en REPLICA
echo      PRINCIPAL aun NO esta iniciado
echo ================================================
echo.
echo Presiona ENTER para iniciar el servidor PRINCIPAL...
pause

REM Paso 3: Iniciar Principal (recupera automaticamente)
echo.
echo ================================================
echo [4/5] Iniciando servidor PRINCIPAL en puerto 8001
echo      El servidor bloqueara operaciones durante
echo      la sincronizacion con la REPLICA
echo ================================================
start "PRINCIPAL (8001)" cmd /k "cd C:\Users\anton\IdeaProjects\psp\src && echo Servidor PRINCIPAL iniciando... && java ProyectoLinda.server.ServidorNodo 8001"
timeout /t 7

echo.
echo ================================================
echo [5/5] Sincronizacion completada!
echo ================================================
echo.
echo El servidor PRINCIPAL ahora tiene los 10 mensajes
echo que fueron escritos en la REPLICA.
echo.
echo Puedes verificar enviando mas mensajes:
echo   java ProyectoLinda.cliente.ClientePrueba localhost 8001 5
echo.
echo Presiona ENTER para finalizar...
pause

