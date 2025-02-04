module AsyncUpFs.Core

open System
open System.Timers
open Constants

let timer: Timer =
    new Timer(default_interval)

let timedEvent: Async<unit> =
    Async.AwaitEvent (timer.Elapsed)
    |> Async.Ignore

[<EntryPoint>]
let main argv =
    printfn "waiting for timer at %O" DateTime.Now.TimeOfDay
    timer.Start()
    printfn "doing something"
    Async.RunSynchronously timedEvent
    printfn "timer ticket at %O" DateTime.Now.TimeOfDay
    0 // Return an integer exit code
