open System
open System.IO
open UglyToad.PdfPig
open UglyToad.PdfPig.Core
open UglyToad.PdfPig.Content
open UglyToad.PdfPig.Fonts.Standard14Fonts
open UglyToad.PdfPig.Writer

(* READ *)
let dir = Directory.GetCurrentDirectory() + @"\"
let file = "sample.pdf"
let doc = PdfDocument.Open(dir + file)
let version = doc.Version 
let page_count = doc.NumberOfPages
let first_page = doc.GetPage(1)
let page_size = first_page.Size
let page_text = first_page.Text

(* WRITE *)
let builder = new PdfDocumentBuilder()
let new_page = builder.AddPage(PageSize.Letter)
let font = builder.AddStandard14Font(Standard14Font.Helvetica);
let point = new PdfPoint(25, 700)
let text_line = "Hello World, Darren!"
new_page.AddText(text_line, 12.0M, point, font) |> ignore
let doc_bytes = builder.Build();
File.WriteAllBytes(dir + "new.pdf", doc_bytes);

[<EntryPoint>]
let main argv =
    printfn "Hello World from F#!"
    printfn "%A" version 
    printfn "%i" page_count
    printfn "%A" page_size
    printfn "%s" page_text
    0 // return an integer exit code
