dfpagent
========

DFP Agent for WebSphere



Implemented DFP features
------------------------

The DFP Agent developed by this project implements the subset of the Dynamic Feedback Protocol that is used by
Cisco Content Service Switches (CSS). This includes the following message types:
 * DFP Parameters (inbound)
 * Preference Information (outbound)
The agent doesn't support the following message types:
 * Server State: unfortunately CSS never sends Server State messages
 * BindID Request, BindID Report and BindID Change Notify: CSS doesn't seem to make use of the Bind-ID concept
