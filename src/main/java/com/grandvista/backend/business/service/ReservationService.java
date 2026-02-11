package com.grandvista.backend.business.service;

import com.grandvista.backend.data.model.Guest;
import com.grandvista.backend.data.model.Reservation;
import com.grandvista.backend.data.repository.GuestRepository;
import com.grandvista.backend.data.repository.ReservationRepository;
import com.grandvista.backend.presentation.dto.CreateBookingRequest;

import java.util.Optional;

public class ReservationService {
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(CreateBookingRequest request) {
        // 1. Handle Guest
        Optional<Guest> existingGuest = guestRepository.findByIdentificationId(request.getIdentificationId());
        Guest guest;

        if (existingGuest.isPresent()) {
            guest = existingGuest.get();
            // Update details if needed, or keep existing. For now, we update contact info
            guest.setContactNumber(request.getContactNumber());
            guest.setAddress(request.getAddress());
            guest.setFullName(request.getFullName());
            guest.setAge(request.getAge());
            guestRepository.save(guest);
        } else {
            guest = new Guest();
            guest.setFullName(request.getFullName());
            guest.setAge(request.getAge());
            guest.setContactNumber(request.getContactNumber());
            guest.setAddress(request.getAddress());
            guest.setIdentificationId(request.getIdentificationId());
            guestRepository.save(guest);
        }

        // 2. Create Reservation
        Reservation reservation = new Reservation();
        reservation.setGuestId(guest.getId());
        reservation.setRoomType(request.getRoomType());
        reservation.setNumberOfPeople(request.getPeople());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setBedType(request.getBedType());
        reservation.setBreakfastIncluded(request.isBreakfastIncluded());

        return reservationRepository.save(reservation);
    }

    public java.util.List<com.grandvista.backend.presentation.dto.ReservationDetailsResponse> getAllReservationsWithDetails() {
        java.util.List<Reservation> reservations = reservationRepository.getAll();
        java.util.List<com.grandvista.backend.presentation.dto.ReservationDetailsResponse> detailsList = new java.util.ArrayList<>();

        for (Reservation res : reservations) {
            com.grandvista.backend.presentation.dto.ReservationDetailsResponse details = new com.grandvista.backend.presentation.dto.ReservationDetailsResponse();
            details.setId(res.getId());
            details.setRoomType(res.getRoomType());
            details.setStatus(res.getStatus());
            details.setStayDuration(res.getCheckInDate().toString() + " to " + res.getCheckOutDate().toString());

            Optional<Guest> guest = guestRepository.findById(res.getGuestId());
            if (guest.isPresent()) {
                details.setClientName(guest.get().getFullName());
                details.setClientContact(guest.get().getContactNumber());
            } else {
                details.setClientName("Unknown Client");
                details.setClientContact("N/A");
            }
            detailsList.add(details);
        }
        return detailsList;
    }

    public boolean deleteReservation(String id) {
        return reservationRepository.delete(id);
    }
}
