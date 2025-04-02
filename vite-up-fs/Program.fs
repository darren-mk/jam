open Browser

let div = document.createElement "div"
div.innerHTML <- "Yay, Darren!"
document.body.appendChild div |> ignore
