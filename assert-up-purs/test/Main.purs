module Test.Main where

import Prelude
import Effect (Effect)
import Test.Assert (assert)
import Main (mult)
 
main :: Effect Unit
main = do
  assert $ 1 + 1 == 2
  assert $ mult 2 3 == 6
