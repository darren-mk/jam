module Main where

import Prelude
import Effect (Effect)
import Halogen (ClassName(..), Component, defaultEval, mkComponent, mkEval)
import Halogen.Aff (awaitBody, runHalogenAff)
import Halogen.HTML as HH
import Halogen.HTML.Properties as HP
import Halogen.VDom.Driver (runUI)

main :: Effect Unit
main = runHalogenAff do
  body <- awaitBody
  runUI component unit body

element = HH.h1 [ ] [ HH.text "Hello, world" ]

input = HH.input [ HP.placeholder "Name" ]

cn = "card w-96 bg-base-100 shadow-xl"

btn =
  HH.button
    [ HP.classes [ HH.ClassName "btn btn-secondary" ] ]
    [ HH.text "yes!" ]

view =
  HH.div
    [ HP.class_ $ ClassName cn ]
    [ HH.text "Hello Halogen!!", btn ]

component :: forall q i o m. Component q i o m
component =
  mkComponent
    { initialState : \_ -> unit
    , render : \_ -> view
    , eval : mkEval $ defaultEval }
