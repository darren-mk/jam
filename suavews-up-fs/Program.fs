open System
open Suave
open Suave.Operators
open Suave.Filters
open Suave.Logging
open Suave.Sockets
open Suave.Sockets.Control
open Suave.WebSocket

let ws (websocket: WebSocket) (ctx: HttpContext) =
    socket {
        let mutable loop = true
        while loop do
            let! msg = websocket.read()
            match msg with
            | (Text, data, true) ->
                let str = UTF8.toString data
                let resp = $"response to %s{str}"
                let byte_resp =
                    resp
                    |> System.Text.Encoding.UTF8.GetBytes
                    |> ByteSegment
                do! websocket.send Text byte_resp true
            | (Close, _, _) ->
                let empty_resp = [||] |> ByteSegment
                do! websocket.send Close empty_resp true
                loop <- false
            | _ -> () }
let app: WebPart =
    choose [
        path "/websocket" >=> handShake ws ]

[<EntryPoint>]
let main _ =
    startWebServer { defaultConfig with logger = Targets.create Verbose [||] } app
    0