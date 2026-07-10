output "vm_fqdn" {
  value       = module.infrastructure.vm_fqdn
  description = "Full FQDN of the VM public IP"
}

output "vm_public_ip" {
  value       = module.infrastructure.vm_public_ip
  description = "Public IP address of the VM"
}

output "acr_login_server" {
  value       = module.infrastructure.acr_login_server
  description = "Login server of the Azure Container Registry"
}

output "pg_server_name" {
  value       = module.infrastructure.pg_server_name
  description = "Name of the PostgreSQL flexible server"
}

output "pg_server_fqdn" {
  value       = module.infrastructure.pg_server_fqdn
  description = "FQDN of the PostgreSQL flexible server"
}

output "db_name" {
  value       = module.infrastructure.db_name
  description = "Name of the application database"
}

output "storage_account_name" {
  value       = module.infrastructure.storage_account_name
  description = "Name of the storage account"
}
