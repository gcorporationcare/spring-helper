package com.gcorp.field.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.gcorp.ApiStarter;
import com.gcorp.field.Country;
import com.gcorp.field.FaxNumber;
import com.gcorp.field.HomeNumber;
import com.gcorp.field.MobileNumber;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Office;
import com.gcorp.notest.repository.OfficeRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ConverterTest {

	@Autowired
	OfficeRepository officeRepository;

	@Test
	public void testConverter() {
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
