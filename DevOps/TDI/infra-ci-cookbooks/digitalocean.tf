variable "ssh_keys_id" {}

resource "digitalocean_droplet" "webapp" {
	image = "centos-7-x64"
	name = "webapp"
	region = "sgp1"
	size = "512mb"
	ssh_keys = [ "${var.ssh_keys_id}" ]

	provisioner "local-exec" {
		command = "echo ${digitalocean_droplet.webapp.ipv4_address} ${digitalocean_droplet.webapp.name} >> /tmp/hosts.txt"
	}
}

resource "digitalocean_droplet" "logapp" {
	image = "centos-7-x64"
	name = "logapp"
	region = "sgp1"
	size = "512mb"
	ssh_keys = [ "${var.ssh_keys_id}" ]

	provisioner "local-exec" {
		command = "echo ${digitalocean_droplet.logapp.ipv4_address} ${digitalocean_droplet.logapp.name} >> /tmp/hosts.txt"
	}
}
