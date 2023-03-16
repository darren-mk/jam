module Index exposing (..)

import Html exposing (..)
import Html.Attributes exposing (..)
import Material.Button as Button
import Material.TopAppBar as TopAppBar

luck = "my luck is love"

greet s = "hi!"

type Msg = Clicked

type Icon = Favorite
          | Love

getIconStr icon =
    case icon of
        Favorite -> "favorite"
        Love -> "favorite"

topAppBar =
    TopAppBar.regular TopAppBar.config
        [ TopAppBar.row []
              [ TopAppBar.section [ TopAppBar.alignStart ]
                    [ Html.span [ TopAppBar.title ]
                          [ text "Title" ] ] ] ]

view model =
    div [ class "jumbotron" ]
        [ topAppBar
        , h1 [] [ text "Welcome to Dunder Mifflin!!!!" ]
        , Button.outlined (Button.config
                          |> Button.setIcon (Just (Button.icon (getIconStr Favorite)))
                          |> Button.setOnClick Clicked) "Hekulio!"
        , p []
            [ text "Dunder Mifflin Inc! (stock symbol "
            , strong [] [ text "DMI" ]
            , text <|
                """
                ) is a micro-cap regional paper and office
                supply distributor
                """ ] ]

main = view "dummy model"
