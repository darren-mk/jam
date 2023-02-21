{-# LANGUAGE DataKinds #-}
{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE TypeOperators #-}

module Main where

import Data.Aeson
import GHC.Generics
import Network.Wai
import Network.Wai.Handler.Warp
import Servant
import System.IO

-- * api

type TotalApi =
  "user" :> Get '[JSON] [User] :<|>
  "item" :> Get '[JSON] [Item] :<|>
  "item" :> Capture "itemId" Integer :> Get '[JSON] Item

totalApi :: Proxy TotalApi
totalApi = Proxy


-- * app

main :: IO ()
main = do
  let port = 3000
      settings =
        setPort port $
        setBeforeMainLoop (hPutStrLn stderr ("listening on port " ++ show port))
        defaultSettings
  runSettings settings =<< mkApp

mkApp :: IO Application
mkApp = return $ serve totalApi server

server :: Server TotalApi
server =
  getUsers :<|>
  getItems :<|>
  getItemById

getItems :: Handler [Item]
getItems = return [exampleItem]

getItemById :: Integer -> Handler Item
getItemById = \ case
  0 -> return exampleItem
  _ -> throwError err404

exampleItem :: Item
exampleItem = Item 123 "example item"

exampleUser :: User
exampleUser = User 1 "Darren" "Kim"

getUsers :: Handler [User]
getUsers = return [exampleUser]

data Item
  = Item {
    itemId :: Integer,
    itemText :: String }
  deriving (Eq, Show, Generic)

instance ToJSON Item
instance FromJSON Item

data User = User 
  { id :: Int
  , firstName :: String
  , lastName :: String }
  deriving (Eq, Show, Generic)
  
instance ToJSON User
instance FromJSON User
