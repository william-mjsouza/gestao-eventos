package com.gestaoeventos.apresentacao.inscricao;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestaoeventos.dominio.inscricao.cupom.CupomServico;

@RestController
@RequestMapping("backend/cupom")
class CupomController {
    private @Autowired CupomServico cupomServico;
    @RequestMapping(method = POST, path = "validar")
    void validar(@RequestBody CupomDto dto) {
        cupomServico.validarEAplicar(dto.codigoCupom, dto.cpfUsuario, dto.eventoId);
    }

    static class CupomDto {
        public String codigoCupom;
        public String cpfUsuario;
        public Long eventoId;
    }
}