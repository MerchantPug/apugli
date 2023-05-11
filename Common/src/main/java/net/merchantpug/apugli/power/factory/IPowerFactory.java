package net.merchantpug.apugli.power.factory;

public interface IPowerFactory<P> {
    
    Class<P> getPowerClass();
    
}
