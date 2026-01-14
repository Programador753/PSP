@echo off
echo ================================================
echo PRUEBA DE SINCRONIZACION CON SEMAFORO
echo ================================================
echo.
echo Este script demuestra:
echo 1. Servidor replica (8004) inicia primero
echo 2. Cliente escribe datos en la replica
echo 3. Servidor principal (8001) se inicia y recupera datos
echo 4. Durante la sincronizacion, las operaciones estan bloqueadas
echo.
echo ================================================
pause

echo.
echo [1/4] Iniciando servidor REPLICA en puerto 8004...
start "Servidor REPLICA (8004)" cmd /k "cd C:\Users\anton\IdeaProjects\psp\src && java ProyectoLinda.server.ServidorNodo 8004"
timeout /t 3

echo.
echo [2/4] La replica esta lista. Ahora puedes ejecutar clientes para agregar datos.
echo        Comando: java ProyectoLinda.cliente.AplicacionCliente localhost 8004
echo.
echo Presiona ENTER cuando hayas agregado datos a la replica...
pause

echo.
echo [3/4] Iniciando servidor PRINCIPAL en puerto 8001...
echo      El servidor principal bloqueara operaciones durante la sincronizacion.
echo.
start "Servidor PRINCIPAL (8001)" cmd /k "cd C:\Users\anton\IdeaProjects\psp\src && java ProyectoLinda.server.ServidorNodo 8001"
timeout /t 5

echo.
echo [4/4] Sincronizacion completada.
echo      El servidor principal ahora tiene los datos de la replica.
echo      Puedes ejecutar clientes contra el servidor principal (8001).
echo.
echo Presiona ENTER para finalizar...
pause

