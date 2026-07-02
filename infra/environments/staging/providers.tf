terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }

  cloud {
    organization = "medai"
    workspaces {
      name = "medai-staging"
    }
  }
}

provider "azurerm" {
  features {}
}
