variable "location" {
  type        = string
  description = "Azure region for all resources"
  default     = "South Africa North"
}

variable "vm_size" {
  type        = string
  description = "Size of the virtual machine"
  default     = "Standard_B2als_v2"
}

variable "acr_sku" {
  type        = string
  description = "SKU for the Azure Container Registry"
  default     = "Basic"
}

variable "acr_name" {
  type        = string
  description = "Name of the Azure Container Registry (globally unique)"
  default     = "medaigradprojrepo"
}

variable "domain_name_label" {
  type        = string
  description = "DNS label for the VM public IP"
  default     = "medai-staging"
}

variable "ssh_public_key" {
  type        = string
  description = "Public SSH key content for the virtual machine"
  sensitive   = true
}

variable "certbot_email" {
  type        = string
  description = "Email for Let's Encrypt certificate registration"
  sensitive   = true
}
