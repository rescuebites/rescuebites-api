package com.rescuebites.api.payment.repositories;

import com.rescuebites.api.payment.data.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM payments p WHERE p.order.orderId = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") UUID orderId);
}