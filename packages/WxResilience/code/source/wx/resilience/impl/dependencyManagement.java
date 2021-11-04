package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import com.ibm.wsdl.util.StringUtils;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.Manifest;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageManager;
// --- <<IS-END-IMPORTS>> ---

public final class dependencyManagement

{
	// ---( internal utility methods )---

	final static dependencyManagement _instance = new dependencyManagement();

	static dependencyManagement _newInstance() { return new dependencyManagement(); }

	static dependencyManagement _cast(Object o) { return (dependencyManagement)o; }

	// ---( server methods )---




	public static final void removePackageDependencies (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(removePackageDependencies)>> ---
		// @sigtype java 3.5
		// [i] field:1:required onPackages
		IDataMap pipelineMap = new IDataMap(pipeline);
		String[] onPackages = pipelineMap.getAsStringArray("onPackages");
		dependencyCache.clear();
		
		if (onPackages != null) {
			Package[] packages = PackageManager.getAllPackages();
			for (Package currentPackage : packages) {
				if (!currentPackage.getName().startsWith("Wm") && !currentPackage.getName().startsWith("Wx")) {
					boolean packageChanged = false;
					Manifest currentManifest = currentPackage.getManifest();
					String[] requires = currentManifest.getRequiresAsStr();
				
					if (requires != null) {
						for (String require : requires) {
							for (String onPackage : onPackages) {
								if (require.contains(onPackage)) {
									currentManifest.delDependency(require);
									dependencyCache.add(new String[]{currentPackage.getName(), require});
									packageChanged = true;
								}
							}
						}
					}
					if (packageChanged) {
						currentPackage.setManifest(currentManifest);
					}
				}
			}
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void restoreLastPackageDepencencies (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(restoreLastPackageDepencencies)>> ---
		// @sigtype java 3.5
		for (String[] depencency : dependencyCache) {
			String currentPackageName = depencency[0];
			String require = depencency[1];
			Package currentPackage = PackageManager.getPackage(currentPackageName);
			if (currentPackage != null) {
				Manifest currentManifest = currentPackage.getManifest();
				currentManifest.addDependency(currentPackageName, require, null);
				currentPackage.setManifest(currentManifest);
			}
			
		}
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static ArrayList<String[]> dependencyCache = new ArrayList<String[]>();
		
	// --- <<IS-END-SHARED>> ---
}

