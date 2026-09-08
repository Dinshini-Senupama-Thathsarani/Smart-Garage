package com.smartgarage.api.config;

import com.smartgarage.api.entity.*;
import com.smartgarage.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final VehicleMakeRepository vehicleMakeRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final SparePartCategoryRepository sparePartCategoryRepository;
    private final SupplierRepository supplierRepository;
    private final SparePartRepository sparePartRepository;
    private final MechanicRepository mechanicRepository;

    @Override
    public void run(String... args) {
        seedVehicleMakesAndModels();
        seedServiceCatalog();
        seedSparePartCategoriesAndParts();
        seedSuppliers();
        seedMechanics();
    }

    private void seedVehicleMakesAndModels() {
        if (vehicleMakeRepository.count() > 0) return;

        VehicleMake toyota = vehicleMakeRepository.save(make("Toyota"));
        VehicleMake nissan = vehicleMakeRepository.save(make("Nissan"));
        VehicleMake honda = vehicleMakeRepository.save(make("Honda"));
        VehicleMake suzuki = vehicleMakeRepository.save(make("Suzuki"));
        VehicleMake mitsubishi = vehicleMakeRepository.save(make("Mitsubishi"));

        vehicleModelRepository.save(model(toyota, "Corolla", 2015));
        vehicleModelRepository.save(model(toyota, "Aqua", 2018));
        vehicleModelRepository.save(model(nissan, "Leaf", 2017));
        vehicleModelRepository.save(model(honda, "Vezel", 2016));
        vehicleModelRepository.save(model(suzuki, "Wagon R", 2019));
        vehicleModelRepository.save(model(mitsubishi, "Montero", 2014));

        log.info("Seeded 5 vehicle makes and 6 vehicle models");
    }

    private void seedServiceCatalog() {
        if (serviceCategoryRepository.count() > 0) return;

        ServiceCategory engine = serviceCategoryRepository.save(category("Engine", "Engine diagnostics and repair"));
        ServiceCategory brakes = serviceCategoryRepository.save(category("Brakes", "Brake pad, disc and fluid service"));
        ServiceCategory electrical = serviceCategoryRepository.save(category("Electrical", "Battery, wiring and lighting"));
        ServiceCategory tyres = serviceCategoryRepository.save(category("Tyres & Wheels", "Alignment, balancing, replacement"));
        ServiceCategory general = serviceCategoryRepository.save(category("General Maintenance", "Routine servicing"));

        serviceTypeRepository.save(serviceType(general, "Oil Change", "3500.00", 30));
        serviceTypeRepository.save(serviceType(engine, "Engine Diagnostic Check", "4500.00", 60));
        serviceTypeRepository.save(serviceType(brakes, "Brake Pad Replacement", "6500.00", 45));
        serviceTypeRepository.save(serviceType(electrical, "Battery Replacement", "8000.00", 20));
        serviceTypeRepository.save(serviceType(tyres, "Wheel Alignment", "2500.00", 40));

        log.info("Seeded 5 service categories and 5 service types");
    }

    private void seedSparePartCategoriesAndParts() {
        if (sparePartCategoryRepository.count() > 0) return;

        SparePartCategory filters = sparePartCategoryRepository.save(partCategory("Filters"));
        SparePartCategory brakesCat = sparePartCategoryRepository.save(partCategory("Brake Parts"));
        SparePartCategory electricalCat = sparePartCategoryRepository.save(partCategory("Electrical"));
        SparePartCategory engineCat = sparePartCategoryRepository.save(partCategory("Engine Parts"));
        SparePartCategory fluids = sparePartCategoryRepository.save(partCategory("Fluids & Lubricants"));

        sparePartRepository.save(sparePart(filters, "Oil Filter", "OF-1001", "850.00", 20, 5));
        sparePartRepository.save(sparePart(brakesCat, "Brake Pad Set (Front)", "BP-2001", "4500.00", 12, 4));
        sparePartRepository.save(sparePart(electricalCat, "12V Car Battery", "BT-3001", "18500.00", 6, 2));
        sparePartRepository.save(sparePart(engineCat, "Spark Plug", "SP-4001", "650.00", 30, 8));
        sparePartRepository.save(sparePart(fluids, "Engine Oil (4L)", "EO-5001", "5200.00", 15, 5));

        log.info("Seeded 5 spare part categories and 5 spare parts");
    }

    private void seedSuppliers() {
        if (supplierRepository.count() > 0) return;

        supplierRepository.save(supplier("Lanka Auto Parts", "Sunil Perera", "0112223344", "sales@lankaautoparts.lk", "Colombo 10"));
        supplierRepository.save(supplier("Colombo Motor Spares", "Nimal Fernando", "0112558822", "info@colombomotorspares.lk", "Wellawatte"));
        supplierRepository.save(supplier("AutoZone Lanka", "Kamal Silva", "0714412233", "contact@autozonelk.com", "Kandy"));
        supplierRepository.save(supplier("Speedline Traders", "Dilani Jayasuriya", "0777112233", "speedline@traders.lk", "Negombo"));
        supplierRepository.save(supplier("Prime Auto Imports", "Ruwan Bandara", "0332255667", "prime@autoimports.lk", "Gampaha"));

        log.info("Seeded 5 suppliers");
    }

    private void seedMechanics() {
        if (mechanicRepository.count() > 0) return;

        mechanicRepository.save(mechanic("Nimal Silva", "Engine Specialist", "0779876543", LocalDate.of(2020, 3, 1)));
        mechanicRepository.save(mechanic("Kasun Rathnayake", "Brakes & Suspension", "0771234567", LocalDate.of(2021, 6, 15)));
        mechanicRepository.save(mechanic("Sampath Perera", "Electrical Systems", "0765554433", LocalDate.of(2019, 1, 10)));
        mechanicRepository.save(mechanic("Chamara Dias", "General Mechanic", "0713332211", LocalDate.of(2022, 9, 5)));
        mechanicRepository.save(mechanic("Ruwan Kumara", "Tyres & Alignment", "0754445566", LocalDate.of(2020, 11, 20)));

        log.info("Seeded 5 mechanics");
    }

    // ---------- tiny builders ----------
    private VehicleMake make(String name) { VehicleMake m = new VehicleMake(); m.setName(name); return m; }

    private VehicleModel model(VehicleMake make, String name, int yearFrom) {
        VehicleModel m = new VehicleModel(); m.setMake(make); m.setModelName(name); m.setYearFrom(yearFrom); return m;
    }

    private ServiceCategory category(String name, String desc) {
        ServiceCategory c = new ServiceCategory(); c.setName(name); c.setDescription(desc); return c;
    }

    private ServiceType serviceType(ServiceCategory cat, String name, String price, int minutes) {
        ServiceType s = new ServiceType();
        s.setCategory(cat); s.setName(name); s.setBasePrice(new BigDecimal(price)); s.setEstimatedMinutes(minutes);
        return s;
    }

    private SparePartCategory partCategory(String name) { SparePartCategory c = new SparePartCategory(); c.setName(name); return c; }

    private SparePart sparePart(SparePartCategory cat, String name, String number, String price, int stock, int reorder) {
        SparePart p = new SparePart();
        p.setCategory(cat); p.setPartName(name); p.setPartNumber(number);
        p.setUnitPrice(new BigDecimal(price)); p.setStockQty(stock); p.setReorderLevel(reorder);
        return p;
    }

    private Supplier supplier(String name, String contact, String phone, String email, String address) {
        Supplier s = new Supplier();
        s.setName(name); s.setContactPerson(contact); s.setPhone(phone); s.setEmail(email); s.setAddress(address);
        return s;
    }

    private Mechanic mechanic(String name, String spec, String phone, LocalDate hireDate) {
        Mechanic m = new Mechanic();
        m.setFullName(name); m.setSpecialization(spec); m.setPhone(phone); m.setHireDate(hireDate);
        return m;
    }
}
