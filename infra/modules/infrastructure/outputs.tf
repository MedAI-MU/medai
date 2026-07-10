output "vm_fqdn" {
  value       = azurerm_public_ip.vm_ip.fqdn
  description = "Full FQDN of the VM public IP"
}

output "vm_public_ip" {
  value       = azurerm_public_ip.vm_ip.ip_address
  description = "Public IP address of the VM"
}

output "acr_login_server" {
  value       = local.acr_login_server
  description = "Login server of the Azure Container Registry"
}

output "resource_group_name" {
  value       = azurerm_resource_group.main.name
  description = "Name of the resource group"
}

output "pg_server_name" {
  value       = azurerm_postgresql_flexible_server.db.name
  description = "Name of the PostgreSQL flexible server"
}

output "pg_server_fqdn" {
  value       = azurerm_postgresql_flexible_server.db.fqdn
  description = "FQDN of the PostgreSQL flexible server"
}

output "db_name" {
  value       = azurerm_postgresql_flexible_server_database.app.name
  description = "Name of the application database"
}

output "pg_admin_login" {
  value       = var.pg_admin_login
  sensitive   = true
  description = "PostgreSQL administrator login (DB_USERNAME)"
}

output "pg_admin_password" {
  value       = var.pg_admin_password
  sensitive   = true
  description = "PostgreSQL administrator password (DB_PASSWORD)"
}

output "storage_account_name" {
  value       = azurerm_storage_account.main.name
  description = "Name of the storage account"
}
