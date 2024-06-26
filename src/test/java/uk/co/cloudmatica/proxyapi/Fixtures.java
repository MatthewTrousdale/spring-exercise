package uk.co.cloudmatica.proxyapi;

import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.repo.model.Company;
import uk.co.cloudmatica.proxyapi.repo.model.Officer;

import java.util.List;

import static java.util.List.of;

public class Fixtures {

    public static CompanyDto getCompanyDtoFixture(String name) {

        return CompanyDto
            .builder()
            .totalResults(1)
            .companies(of(getCompanyFixture(name)))
            .build();
    }

    public static Company getCompanyFixture(String name) {

        return Company
            .builder()
            .title("Cloudmatica ltd")
            .companyNumber("1234")
            .officers(getOfficersFixture(name))
            .build();
    }

    public static List<Officer> getOfficersFixture(String name) {

        return of(Officer
            .builder()
            .name(name)
            .build());
    }
}
