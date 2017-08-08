package br.com.solucoes161.java.smscarmanager;

/**
 * Created by Melky on 27/01/2016.
 */
public class MapsEvent {
    public interface MapsEventInterface{
        void onReceive(Double lat, Double lng);
    };
}
