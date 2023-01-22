module Main exposing (..)

import Browser as B
import Css as C
import Html.Styled as HS
import Html.Styled.Events as HSE

main : Program () Model Msg
main = B.sandbox { init = init, update = update
                 , view = view >> HS.toUnstyled }

type alias Model = Int

init : Model
init = 0

type Msg = Increment | Decrement

update : Msg -> Model -> Model
update msg model =
  case msg of
    Increment -> model + 1
    Decrement -> model - 1

theme : { secondary : C.Color, primary : C.Color }
theme = { primary = C.hex "55af6a"
        , secondary = C.rgb 250 240 230 }

btn : List (HS.Attribute msg) -> List (HS.Html msg) -> HS.Html msg
btn = HS.styled HS.button
      [ C.margin (C.px 12)
      , C.fontSize (C.px 16)
      , C.boxShadow4 (C.px 1) (C.px 1) (C.px 0) (C.px 0)
      , C.padding2 (C.em 0.25) (C.em 0.5)
      , C.letterSpacing (C.px 2)
      , C.textDecoration C.none
      , C.textTransform C.uppercase
      , C.position C.relative
      , C.color (C.rgb 0 0 0)
      , C.hover
            [ C.backgroundColor theme.primary
            , C.textDecoration C.underline ] ]

view : Model -> HS.Html Msg
view model =
  HS.div []
    [ btn [ HSE.onClick Decrement ] [ HS.text "subtract" ]
    , HS.div [] [ HS.text (String.fromInt model) ]
    , btn [ HSE.onClick Increment ] [ HS.text "add" ] ]
