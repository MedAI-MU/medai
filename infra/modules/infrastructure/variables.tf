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

variable "existing_acr_resource_group_name" {
  type        = string
  description = "Resource group name of the shared, externally-managed ACR"
}

variable "pg_version" {
  type        = string
  description = "PostgreSQL major version for the flexible server"
  default     = "18"
}

variable "pg_admin_login" {
  type        = string
  description = "Administrator login for the PostgreSQL flexible server"
  default     = "medai"
}

variable "pg_admin_password" {
  type        = string
  description = "Administrator password for the PostgreSQL flexible server"
  sensitive   = true
}

variable "pg_sku_name" {
  type        = string
  description = "SKU name (tier_family_size) for the flexible server — Burstable B2s"
  default     = "B_Standard_B2s"
}

variable "pg_storage_mb" {
  type        = number
  description = "Storage in MB for the flexible server (128 GiB = 131072)"
  default     = 131072
}

variable "pg_storage_tier" {
  type        = string
  description = "Storage tier (P10 = 128 GiB @ 500 IOPS)"
  default     = "P10"
}

variable "pg_backup_retention_days" {
  type        = number
  description = "Backup retention in days (7–35)"
  default     = 7
}

variable "client_ip_address" {
  type        = string
  description = "Public IP (single IPv4) to allow in the flexible-server firewall rule named client-ip-address"
  sensitive   = true
}
