package com.kafka.intermediario.services.implementacao;

import com.kafka.intermediario.entities.Banco;
import com.kafka.intermediario.services.BancoService;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BancoServiceImpl extends AbstractService<Banco, UUID> implements BancoService<Banco, UUID> {

    @Override
    public List<Banco> bancos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Banco editar(UUID pk_banco, @NotNull Banco banco){
        banco.setPk_banco(pk_banco);
        return super.editar(pk_banco, banco);
    }

}
