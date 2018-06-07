#
# Cookbook:: td-agent2
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

cookbook_file "/etc/yum.repos.d/td.repo" do
	owner  "root"
	group  "root"
	mode   "0644"
	source "/etc/yum.repos.d/td.repo"
end

package "td-agent" do
	action :install
end

service "td-agent" do
	supports :status => true, :restart => true, :reload => true
	action [ :enable, :start ]
end
