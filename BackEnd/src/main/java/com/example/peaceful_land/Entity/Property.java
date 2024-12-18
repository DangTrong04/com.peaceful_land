package com.example.peaceful_land.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "properties")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Property extends BaseEntity {

    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private Account user;

    @Column
    private Boolean offer; // Hình thức: 0 - Mua bán, 1 - Cho thuê

    @Column
    private Boolean status; // Trạng thái: 0 - Đã bán hoặc cho thuê, 1 - sẵn sàng

    @Column(name = "rental_period")
    private LocalDate rentalPeriod; // Thời gian thuê

    @Column
    private String location;

    @Column(name = "location_detail")
    private String locationDetail;

    @Column(name = "map_url")
    private String mapUrl;

    @Column
    private String category;

    @Column
    private Long price;

    @Column
    private Integer area;

    @Column
    private String legal;

    @Column
    private Integer bedrooms;

    @Column
    private Integer toilets;

    @Column
    private Byte entrance;

    @Column
    private Byte frontage;

    @Column(name = "house_orientation")
    private String houseOrientation;

    @Column(name = "balcony_orientation")
    private String balconyOrientation;

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", userId=" + user.getId() +
                ", offer=" + offer +
                ", status=" + status +
                ", rentalPeriod=" + rentalPeriod +
                ", location='" + location + '\'' +
                ", locationDetail='" + locationDetail + '\'' +
                ", mapUrl='" + mapUrl + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", area=" + area +
                ", legal='" + legal + '\'' +
                ", bedrooms=" + bedrooms +
                ", toilets=" + toilets +
                ", entrance=" + entrance +
                ", frontage=" + frontage +
                ", houseOrientation='" + houseOrientation + '\'' +
                ", balconyOrientation='" + balconyOrientation + '\'' +
                '}';
    }

    public PropertyLog toPropertyLog(){
        return PropertyLog.builder()
                .property(this)
                .action("Cập nhật giá")
                .offer(this.offer)
                .status(this.status)
                .rentalPeriod(this.rentalPeriod)
                .price(this.price)
                .build();
    }
}