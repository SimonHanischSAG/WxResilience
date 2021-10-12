# WxResilience
webMethods IntegrationServer package for implementing resilient and robust services.

It is designed for usage together with the official packages WxConfig (or the free alternative https://github.com/SimonHanischSAG/WxConfigLight) and optionally together with the official packages WxLog or WxLog2.

MANY THANKS TO LIDL AND SCHWARZ IT, who kindly allowed to provide the template for this package and make it public.

How to use (basic configuration):

1. Provide WxConfig or WxConfigLight (compare with above).

2. Deploy/checkout WxResilience/WxResilience_Test

3. Build:

If you checkout the sources from GitHub you have to compile the source, e.g. with:
C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience
C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience_Test

4. Reload WxResilience

5. Check "C:\SoftwareAG\IntegrationServer\instances\default\logs\server.log" for entries like:

2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Scanning all packages for ErrorHandling.xml files 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Validating the summarized error handling 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- ExceptionHandlingSummarized.xml is valid 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Initializing error handling framework... 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Successfully initialized error handling 
