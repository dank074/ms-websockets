# NitroWebsockets

## What is NitroWebsockets? ##
NitroWebsockets is a plugin that adds Nitro HTML5 Client compatibility to any hotel running Arcturus MS 2.4.0 and above.

## How do I configure the plugin?
Startup the plugin so it generates the required entries under your `emulator_settings` table. The following fields will be generated:
- `websockets.whitelist` - a comma-delimited list containing all permitted Origin headers. You should write the domain name of your hotel here, since the Websocket connection will be initiated there. Wildcards are also supported, so you can whitelist all subdomains by adding for example: `*.example.com`, or even whitelist all origins by adding `*` (not recommended)
- `ws.nitro.host` - host ip, should leave it as 0.0.0.0
- `ws.nitro.port` - host port, can be any port but if you want to proxy wss traffic with Cloudflare read the following section

## How do I connect to my emulator using Secure Websockets (wss)? ##
You can easily proxy wss traffic using Cloudflare. However, you should first make sure that your `ws.nitro.port` is set to one that is listed as HTTPS Cloudflare Compatible in the following link:
https://support.cloudflare.com/hc/en-us/articles/200169156-Which-ports-will-Cloudflare-work-with-

As of writing this, the following ports are listed as compatible:
- 443
- 2053
- 2083
- 2087
- 2096
- 8443

After your port is set to one that is compatible, create a new A record for a subdomain that will be used for websocket connections, and make sure that it is set to be proxied by Cloudflare (the cloud should be orange if it is being proxied). 

Finally, create a new page rule under the Page Rules tab in Cloudflare and disable SSL for the subdomain you created above. You will now be able to connect using secure websockets using the following example url, where I created an A record for the subdomain `ws` and I set my `ws.nitro.port` to 2096: `wss://ws.example.com:2096` 

## License ##
This plugin is released under the GNU GPLv3