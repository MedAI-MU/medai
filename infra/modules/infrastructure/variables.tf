variable "environment" {
  type        = string
  description = "Environment name (e.g., staging, prod)"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be one of: dev, staging, prod."
  }
}

variable "location" {
  type        = string
  description = "Azure region for all resources"
}

variable "vm_size" {
  type        = string
  description = "Size of the virtual machine"
}

variable "admin_username" {
  type        = string
  description = "Admin username for the virtual machine"
  default     = "azureuser"
}

variable "ssh_public_key" {
  type        = string
  description = "Public SSH key content for the virtual machine"
  sensitive   = true
}

variable "certbot_email" {
  type        = string
  description = "Email used for Let's Encrypt certificate registration"
  sensitive   = true
}

variable "domain_name_label" {
  type        = string
  description = "DNS label for the public IP — produces <label>.<region>.cloudapp.azure.com"
}

variable "acr_name" {
  type        = string
  description = "Name of the Azure Container Registry (globally unique)"
}

variable "acr_sku" {
  type        = string
  description = "SKU for the Azure Container Registry"
  default     = "Basic"
}

variable "create_acr" {
  type        = bool
  description = "Whether to create the ACR in this environment (staging creates it, prod references it)"
  default     = true
}

variable "existing_acr_resource_group_name" {
  type        = string
  description = "Resource group name of an existing ACR (used when create_acr = false)"
  default     = ""
}
