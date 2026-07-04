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

variable "acr_name" {
  type        = string
  description = "Name of the Azure Container Registry (globally unique)"
  default     = "medaigradprojrepo"
}

variable "existing_acr_resource_group_name" {
  type        = string
  description = "Resource group name of the shared, externally-managed ACR"
  default     = "medai-shared-rg"
}

variable "domain_name_label" {
  type        = string
  description = "DNS label for the VM public IP"
  default     = "medai-proj-staging"
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

variable "pg_admin_password" {
  type        = string
  description = "Administrator password for the PostgreSQL flexible server"
  sensitive   = true
}

variable "client_ip_address" {
  type        = string
  description = "Public IP to allow via the client-ip-address firewall rule"
  sensitive   = true
}
