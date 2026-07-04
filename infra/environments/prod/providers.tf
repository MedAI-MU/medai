terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }

  cloud {
    organization = "medai-org"
    workspaces {
      name = "medai-prod"
    }
  }
}

provider "azurerm" {
  features {}
}
