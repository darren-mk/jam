open System
open System.Timers

#if INTERACTIVE
#r "nuget: Npgsql.FSharp, 5.6.0"
#endif

let timer: Timer = new Timer(2000.0)

let timedEvent: Async<unit> = Async.AwaitEvent (timer.Elapsed) |> Async.Ignore

printfn "waiting for timer at %O" DateTime.Now.TimeOfDay

timer.Start()

printfn "doing something"

Async.RunSynchronously timedEvent

printfn "timer ticket at %O" DateTime.Now.TimeOfDay

(*
$ dotnet run
waiting for timer at 21:44:15.5714010
doing something
timer ticket at 21:44:17.6005980
*)
