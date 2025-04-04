open Fable.Core
open Browser

[<Import("hello", "./greet.js")>]
let hello : string -> unit = jsNative

hello "yo!"

[<Global>]
let console: JS.Console = jsNative
console.log "hey, i am console, as you know"

[<Global("console")>]
let logger: JS.Console = jsNative
logger.log "hey, i am named logger."

let div = document.createElement "div"
div.innerHTML <- "Yay, Darren!!"
document.body.appendChild div |> ignore
