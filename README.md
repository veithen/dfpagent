dfpagent
========

_DFP Agent for WebSphere_ is an implementation of Cisco's [Dynamic Feedback Protocol](http://tools.ietf.org/html/draft-eck-dfp-01)
for WebSphere. It adds a DFP agent to the WebSphere runtime to allow it to report status information to a
load balancer supporting that protocol. The code currently only supports reporting two discrete weights:

 * 0 if the application server is not fully started or is stopping (i.e. if the HTTP port is not yet open or
   going to close).
 * 1 if the application server is in state `STARTED` (i.e. if the HTTP port is open).

In a setup where a Cisco load balancer is put directly in front of a WebSphere cluster (without an IBM HTTP Server or
other reverse proxy between them), this makes it possible to restart a WebSphere instance without loosing requests.
This works because the load balancer is immediately aware that the WebSphere instance is going to stop.

The code could easily be extended to calculate weights dynamically, based on parameters such as the CPU load or
the usage of the Web container thread pool.

Project status
--------------

The code was developed as a proof of concept and has been tested successfully. However, it has never been used
in production. The design of the code is very clean and easily extensible (Actually it is somewhat over-engineered
because it was originally expected that more than two DFP message types would need to be supported).

Feel free to clone the project and/or to contact the author if you think the code may be useful to you or
you need additional information.

License
-------

The code is licensed under the GNU Public License, version 2.

Building the project
--------------------

The project is an Eclipse plug-In project. Before importing it into Eclipse, create a target platform definition
from the WebSphere runtime (Note that the project was developed using WAS 7.0, but it may be buildable using
a more recent version) and select it as default. After importing the project, use "Export > Deployable plug-ins
and fragments" to build the plug-in JAR.

Installation and configuration on WebSphere
-------------------------------------------

To install the agent into WebSphere, drop the plug-in JAR into the `plugins` folder of the WebSphere
installation. Then use the `osgiCfgInit` tool to refresh the OSGi caches (otherwise WebSphere may fail to load
the plug-in). To enable the DFP agent, define a new port with name `DFP_AGENT_ADDRESS` and a suitable port number
in the configuration(s) of the WebSphere application server(s) where the agent should run.

Note that each DFP agent instance will report the status of the local application server only. Therefore, a
DFP agent needs to be configured on each application server to which the CSS directs HTTP traffic.

Cisco load balancer configuration
---------------------------------

On the load balancer, use the `dfp` command to add each DFP agent configured on WebSphere. Choose a suitable
`timeout`: this will determine how much time it will take the load balancer to detect that a server has crashed
without properly closing the connection.

Then set the static weight of the services to zero. This is very important because the DFP agent is restarted
together with the WebSphere instance and the load balancer will use the static weight if the DFP agent
is not available. However, we want the effective weight to be non zero only if the WebSphere server is
running and the HTTP port is open.

Implemented DFP features
------------------------

The DFP Agent developed by this project implements the subset of the Dynamic Feedback Protocol that is used by
Cisco Content Service Switches (CSS). This includes the following message types:

 * DFP Parameters (inbound)
 * Preference Information (outbound)

The agent doesn't support the following message types:

 * Server State: unfortunately CSS never sends Server State messages
 * BindID Request, BindID Report and BindID Change Notify: CSS doesn't seem to make use of the Bind-ID concept

In addition, security is currently not supported.
