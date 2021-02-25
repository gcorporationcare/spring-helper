package com.gcorp.field.converter;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
		Assert.assertNotNull(office);
		Office savedOffice = officeRepository.findById(office.getId()).get();
		Assert.assertEquals(faxNumber, savedOffice.getFax());
		Assert.assertEquals(homeNumber, savedOffice.getHome());
		Assert.assertEquals(mobileNumber, savedOffice.getMobile());
	}
}
