@call %~dp0env.cmd
set CP="%EE_HOME%\lib\*"
call java -Dhome=D:\CG -cp %CP% ee.main.EeMain %*
call _duration %STARTTIME%