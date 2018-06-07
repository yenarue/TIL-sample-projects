require 'spec_helper'

describe package('td-agent') do
	it { should be_installed }
end

describe service('td-agent') do
	it { should be_enabled }
	it { should be_running }
end
