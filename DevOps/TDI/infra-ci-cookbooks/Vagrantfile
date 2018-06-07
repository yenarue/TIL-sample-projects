# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
    config.vm.box = "bento/centos-7.1"

	config.vm.define :webapp do |host|
		host.vm.hostname = "webapp"
	end

	config.vm.define :logapp do |host|
		host.vm.hostname = "logapp"
	end
end
