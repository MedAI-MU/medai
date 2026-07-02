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
