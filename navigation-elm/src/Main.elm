module Main exposing (..)

import Browser as B
import Browser.Navigation as BN
import Html as H
import Html.Attributes as HA
import Url as U

main : Program () Model Msg
main = B.application
       { init = init , view = view
       , update = update
       , subscriptions = subscriptions
       , onUrlChange = UrlChanged
       , onUrlRequest = LinkClicked }

type alias Model = { key : BN.Key, url : U.Url }

init : () -> U.Url -> BN.Key -> ( Model, Cmd Msg )
init _ url key = ( Model key url, Cmd.none )

type Msg = LinkClicked B.UrlRequest
         | UrlChanged U.Url

update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
  case msg of
    LinkClicked urlRequest ->
      case urlRequest of
        B.Internal url ->
          ( model, BN.pushUrl model.key (U.toString url) )
        B.External href ->
          ( model, BN.load href )
    UrlChanged url ->
      ( { model | url = url }, Cmd.none )

subscriptions : Model -> Sub Msg
subscriptions _ = Sub.none

view : Model -> B.Document Msg
view model =
  { title = "URL Interceptor"
  , body = [ H.text "The current URL is: "
           , H.b [] [ H.text (U.toString model.url) ]
           , H.ul []
               [ viewLink "/"
               , viewLink "/home"
               , viewLink "/profile"
               , viewLink "/reviews/the-century-of-the-self"
               , viewLink "/reviews/public-opinion"
               , viewLink "/reviews/shah-of-shahs" ] ] }

viewLink : String -> H.Html msg
viewLink path =
  H.li [] [ H.a [ HA.href path ] [ H.text path ] ]

-- reference:
-- https://guide.elm-lang.org/webapps/navigation.html
