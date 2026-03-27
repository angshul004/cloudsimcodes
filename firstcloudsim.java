package firstcloudsim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class firstcloudsim {

    public static void main(String[] args) {

        try {
            // 1. Initialize CloudSim
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            // 2. Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // 3. Create Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");
            int brokerId = broker.getId();

            // 4. Create VM
            List<Vm> vmlist = new ArrayList<>();

            Vm vm = new Vm(0, brokerId, 1000, 1, 512, 1000, 1000,
                    "Xen", new CloudletSchedulerTimeShared());

            vmlist.add(vm);
            broker.submitVmList(vmlist);

            // 5. Create Cloudlet
            List<Cloudlet> cloudletList = new ArrayList<>();

            Cloudlet cloudlet = new Cloudlet(
                    0, 40000, 1, 300, 300,
                    new UtilizationModelFull(),
                    new UtilizationModelFull(),
                    new UtilizationModelFull()
            );

            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);

            broker.submitCloudletList(cloudletList);

            // 6. Start Simulation
            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // 7. Output
            System.out.println("===== OUTPUT =====");
            for (Cloudlet cl : newList) {
                System.out.println("Cloudlet ID: " + cl.getCloudletId() +
                        " Status: " + cl.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {

        List<Host> hostList = new ArrayList<>();

        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));

        hostList.add(new Host(
                0,
                new RamProvisionerSimple(2048),
                new BwProvisionerSimple(10000),
                1000000,
                peList,
                new VmSchedulerTimeShared(peList)
        ));

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristics(
                        "x86", "Linux", "Xen",
                        hostList, 10.0, 3.0,
                        0.05, 0.1, 0.1
                );

        try {
            return new Datacenter(
                    name,
                    characteristics,
                    new VmAllocationPolicySimple(hostList),
                    new LinkedList<>(),
                    0
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}