#
# Cookbook:: base_packages
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.
%w{
	dstat
	git
	tmux
	vim-enhanced
	yum-plugin-versionlock
	zsh
}.each do |pkg|
	package pkg do
		action :install
	end
end
