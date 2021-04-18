package com.github.gcorporationcare.data.field.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.field.Country;
import com.github.gcorporationcare.data.field.FaxNumber;
import com.github.gcorporationcare.data.field.HomeNumber;
import com.github.gcorporationcare.data.field.MobileNumber;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Office;
import com.github.gcorporationcare.notest.repository.OfficeRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ConverterTest {

	@Autowired
	OfficeRepository officeRepository;

	@Test
	void testConverter() {
		final String number = "01";
		final Country country = Country.find("us");
		FaxNumber faxNumber = new FaxNumber(country, number, number, number);
		HomeNumber homeNumber = new HomeNumber(country, number, number, number);
		MobileNumber mobileNumber = new MobileNumber(country, number, number, number);
		Office office = RandomUtils.randomOffice();
		office.setFax(faxNumber);
		office.setHome(homeNumber);
		office.setMobile(mobileNumber);
		office = officeRepository.save(office);
		assertNotNull(office);
		Office savedOffice = officeRepository.findById(office.getId()).get();
		assertEquals(faxNumber, savedOffice.getFax());
		assertEquals(homeNumber, savedOffice.getHome());
		assertEquals(mobileNumber, savedOffice.getMobile());
	}
}
