#r "nuget: FSharp.Data, 6.3.0"
open FSharp.Data

let page: HtmlDocument = 
  HtmlDocument.Load "https://fsprojects.github.io/FSharp.Data/library/HtmlParser.html"

let body: HtmlNode = page.Body()

let finding: HtmlAttribute =
  body.Descendants["a"]
  |> Seq.choose (fun x -> x.TryGetAttribute("href"))
  |> Seq.head

finding
// HtmlAttribute ("href", "https://fsprojects.github.io/FSharp.Data/")