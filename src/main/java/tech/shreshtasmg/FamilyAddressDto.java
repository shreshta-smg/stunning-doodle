package tech.shreshtasmg;

public record FamilyAddressDto(String fullName,
                               String address,
                               String area,
                               String taluk,
                               String phoneNumber,
                               String resourceKey) implements GenericReportRequest {
}
