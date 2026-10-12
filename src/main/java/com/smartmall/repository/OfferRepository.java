package com.smartmall.repository;
import com.smartmall.entity.Offer; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface OfferRepository extends JpaRepository<Offer,Long>{ List<Offer> findByStoreOwnerId(Long id); }
