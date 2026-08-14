package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Clinica;
import com.asistencia_el_salvador.web_app_asistencia.model.DoctorClinica;
import com.asistencia_el_salvador.web_app_asistencia.model.DoctorClinicaId;
import com.asistencia_el_salvador.web_app_asistencia.model.VwClinicaDoctor;
import com.asistencia_el_salvador.web_app_asistencia.repository.ClinicaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.DoctorClinicaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.VwDoctorClinicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicaService {
    private ClinicaRepository clinicaRepository;
    private VwDoctorClinicaRepository vwDoctorClinicaRepository;
    private DoctorClinicaRepository  doctorClinicaRepository;

    public ClinicaService(ClinicaRepository clinicaRepository,
                          VwDoctorClinicaRepository vwDoctorClinicaRepository,
                          DoctorClinicaRepository doctorClinicaRepository) {
        this.clinicaRepository = clinicaRepository;
        this.vwDoctorClinicaRepository = vwDoctorClinicaRepository;
        this.doctorClinicaRepository = doctorClinicaRepository;
    }

    public List<VwClinicaDoctor> getClinicasDoctor(String dui) {
        return vwDoctorClinicaRepository.findByDui(dui);
    }
    public Clinica guardarClinica(Clinica clinica) {
        return clinicaRepository.save(clinica);
    }
    public DoctorClinica guardarDoctorClinica(DoctorClinica doctorClinica) {
        return doctorClinicaRepository.save(doctorClinica);
    }

    public Optional<Clinica> obtenerPorId(Long idClinica) {
        return clinicaRepository.findById(idClinica);
    }

    public Clinica actualizarClinica(Clinica clinica) {
        return clinicaRepository.save(clinica);
    }

    // Verifica que la clínica realmente pertenezca a ese doctor antes de permitir editar
    public boolean perteneceADoctor(Integer idClinica, String dui) {
        return doctorClinicaRepository.existsById(new DoctorClinicaId(idClinica, dui.trim()));
    }
}
