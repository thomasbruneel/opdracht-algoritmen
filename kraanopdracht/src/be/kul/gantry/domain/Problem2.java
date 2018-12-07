package be.kul.gantry.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Problem2 {

    private final int minX, maxX, minY, maxY;
    private final int maxLevels;
    private final List<Item> items;
    private final List<Job> inputJobSequence;
    private final List<Job> outputJobSequence;

    private final List<Gantry> gantries;
    private final List<Slot> slots;
    private final int safetyDistance;
    private final int pickupPlaceDuration;
    
    private HashMap<Integer,Slot> itemToSlot;

    public Problem2(int minX, int maxX, int minY, int maxY, int maxLevels,
                   List<Item> items, List<Gantry> gantries, List<Slot> slots,
                   List<Job> inputJobSequence, List<Job> outputJobSequence, int gantrySafetyDist, int pickupPlaceDuration,HashMap <Integer, Slot> itemToSlot) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.maxLevels = maxLevels;
        this.items = new ArrayList<>(items);
        this.gantries = new ArrayList<>(gantries);
        this.slots = new ArrayList<>(slots);
        this.inputJobSequence = new ArrayList<>(inputJobSequence);
        this.outputJobSequence = new ArrayList<>(outputJobSequence);
        this.safetyDistance = gantrySafetyDist;
        this.pickupPlaceDuration = pickupPlaceDuration;
        this.itemToSlot=itemToSlot;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }


    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Job> getInputJobSequence() {
        return inputJobSequence;
    }

    public List<Job> getOutputJobSequence() {
        return outputJobSequence;
    }

    public List<Gantry> getGantries() {
        return gantries;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public int getSafetyDistance() {
        return safetyDistance;
    }

    public int getPickupPlaceDuration() {
        return pickupPlaceDuration;
    }
    

    public HashMap<Integer, Slot> getItemToSlot() {
		return itemToSlot;
	}

	public void setItemToSlot(HashMap<Integer, Slot> itemToSlot) {
		this.itemToSlot = itemToSlot;
	}

	public void writeJsonFile(File file) throws IOException {
        JSONObject root = new JSONObject();

        JSONObject parameters = new JSONObject();
        root.put("parameters",parameters);

        parameters.put("gantrySafetyDistance",safetyDistance);
        parameters.put("maxLevels",maxLevels);
        parameters.put("pickupPlaceDuration",pickupPlaceDuration);

        JSONArray items = new JSONArray();
        root.put("items",items);

        for(Item item : this.items) {
            JSONObject jo = new JSONObject();
            jo.put("id",item.getId());

            items.add(jo);
        }


        JSONArray slots = new JSONArray();
        root.put("slots",slots);
        for(Slot slot : this.slots) {
            JSONObject jo = new JSONObject();
            jo.put("id",slot.getId());
            jo.put("cx",slot.getCenterX());
            jo.put("cy",slot.getCenterY());
            jo.put("minX",slot.getXMin());
            jo.put("maxX",slot.getXMax());
            jo.put("minY",slot.getYMin());
            jo.put("maxY",slot.getYMax());
            jo.put("z",slot.getZ());
            jo.put("type",slot.getType().name());
            jo.put("itemId",slot.getItem() == null ? null : slot.getItem().getId());

            slots.add(jo);
        }

        JSONArray gantries = new JSONArray();
        root.put("gantries",gantries);
        for(Gantry gantry : this.gantries) {
            JSONObject jo = new JSONObject();

            jo.put("id",gantry.getId());
            jo.put("xMin",gantry.getXMin());
            jo.put("xMax",gantry.getXMax());
            jo.put("startX",gantry.getStartX());
            jo.put("startY",gantry.getStartY());
            jo.put("xSpeed",gantry.getXSpeed());
            jo.put("ySpeed",gantry.getYSpeed());

            gantries.add(jo);
        }

        JSONArray inputSequence = new JSONArray();
        root.put("inputSequence",inputSequence);

        for(Job inputJ : this.inputJobSequence) {
            JSONObject jo = new JSONObject();
            jo.put("itemId",inputJ.getItem().getId());
            jo.put("fromId",inputJ.getPickup().getSlot().getId());

            inputSequence.add(jo);
        }

        JSONArray outputSequence = new JSONArray();
        root.put("outputSequence",outputSequence);

        for(Job outputJ : this.outputJobSequence) {
            JSONObject jo = new JSONObject();
            jo.put("itemId",outputJ.getItem().getId());
            jo.put("toId",outputJ.getPlace().getSlot().getId());

            outputSequence.add(jo);
        }

        try(FileWriter fw = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fw.write(gson.toJson(root));
        }

    }

    public static Problem2 fromJson(File file) throws IOException, ParseException {


        JSONParser parser = new JSONParser();

        try(FileReader reader = new FileReader(file)) {
            JSONObject root = (JSONObject) parser.parse(reader);

            List<Item> itemList = new ArrayList<>();
            List<Slot> slotList = new ArrayList<>();
            List<Gantry> gantryList = new ArrayList<>();
            List<Job> inputJobList = new ArrayList<>();
            List<Job> outputJobList = new ArrayList<>();

            JSONObject parameters = (JSONObject) root.get("parameters");
            int safetyDist = ((Long)parameters.get("gantrySafetyDistance")).intValue();
            int maxLevels = ((Long)parameters.get("maxLevels")).intValue();
            int pickupPlaceDuration = ((Long)parameters.get("pickupPlaceDuration")).intValue();

            JSONArray items = (JSONArray) root.get("items");
            for(Object o : items) {
                int id = ((Long)((JSONObject)o).get("id")).intValue();

                Item c = new Item(id);
                itemList.add(c);
            }


            int overallMinX = Integer.MAX_VALUE, overallMaxX = Integer.MIN_VALUE;
            int overallMinY = Integer.MAX_VALUE, overallMaxY = Integer.MIN_VALUE;

            JSONArray slots = (JSONArray) root.get("slots");
            
            HashMap<Integer,Slot>itemToSlot=new HashMap<>();
            
            for(Object o : slots) {
                JSONObject slot = (JSONObject) o;

                int id = ((Long)slot.get("id")).intValue();
                int cx = ((Long)slot.get("cx")).intValue();
                int cy = ((Long)slot.get("cy")).intValue();
                int minX = ((Long)slot.get("minX")).intValue();
                int minY = ((Long)slot.get("minY")).intValue();
                int maxX = ((Long)slot.get("maxX")).intValue();
                int maxY = ((Long)slot.get("maxY")).intValue();
                int z = ((Long)slot.get("z")).intValue();

                overallMinX = Math.min(overallMinX,minX);
                overallMaxX = Math.max(overallMaxX,maxX);
                overallMinY = Math.min(overallMinY,minY);
                overallMaxY = Math.max(overallMaxY,maxY);

                Slot.SlotType type = Slot.SlotType.valueOf((String)slot.get("type"));
                Integer itemId = slot.get("itemId") == null ? null : ((Long)slot.get("itemId")).intValue();
                Item c = itemId == null ? null : itemList.get(itemId);

                Slot s = new Slot(id,cx,cy,minX,maxX,minY,maxY,z,type,c);
                slotList.add(s);
                
                //itemID toekennen aan slot als slot een item bevat
                if(c!=null){
                	itemToSlot.put(c.getId(),s);
                }
            }


            JSONArray gantries = (JSONArray) root.get("gantries");
            for(Object o : gantries) {
                JSONObject gantry = (JSONObject) o;


                int id = ((Long)gantry.get("id")).intValue();
                int xMin = ((Long)gantry.get("xMin")).intValue();
                int xMax = ((Long)gantry.get("xMax")).intValue();
                int startX = ((Long)gantry.get("startX")).intValue();
                int startY = ((Long)gantry.get("startY")).intValue();
                double xSpeed = ((Double)gantry.get("xSpeed")).doubleValue();
                double ySpeed = ((Double)gantry.get("ySpeed")).doubleValue();

                Gantry g = new Gantry(id, xMin, xMax, startX, startY, xSpeed, ySpeed);
                gantryList.add(g);
            }

            JSONArray inputJobs = (JSONArray) root.get("inputSequence");
            int jid = 0;
            for(Object o : inputJobs) {
                JSONObject inputJob = (JSONObject) o;

                int iid = ((Long) inputJob.get("itemId")).intValue();
                int sid = ((Long) inputJob.get("fromId")).intValue();

                Job job = new Job(jid++,itemList.get(iid),slotList.get(sid),null);
                inputJobList.add(job);
            }

            JSONArray outputJobs = (JSONArray) root.get("outputSequence");
            for(Object o : outputJobs) {
                JSONObject outputJob = (JSONObject) o;

                int iid = ((Long) outputJob.get("itemId")).intValue();
                int sid = ((Long) outputJob.get("toId")).intValue();

                Job job = new Job(jid++,itemList.get(iid),null, slotList.get(sid));
                outputJobList.add(job);
            }


            return new Problem2(
                    overallMinX,
                    overallMaxX,
                    overallMinY,
                    overallMaxY,
                    maxLevels,
                    itemList,
                    gantryList,
                    slotList,
                    inputJobList,
                    outputJobList,
                    safetyDist,
                    pickupPlaceDuration, 
                    itemToSlot);
        }
    }



    public ArrayList<Move> solve() {

        ArrayList<Move> solution = new ArrayList<>();

        Slot inputslot = new Slot(-1,-1,-1,-1,-1,-1,-1,-1,Slot.SlotType.STORAGE,null),outputslot = new Slot(-1,-1,-1,-1,-1,-1,-1,-1,Slot.SlotType.STORAGE,null);
        Gantry inputGantry=gantries.get(0);
        Gantry outputGantry=gantries.get(1);
        

        Map<Integer,SlotTree> rows = new HashMap<>();
        for (Slot s : slots){
            if (s.getType() != Slot.SlotType.STORAGE){
                if (s.getType() == Slot.SlotType.INPUT) inputslot = s;
                else outputslot = s;
            } else {
                if (!rows.containsKey(s.getCenterY())) rows.put(s.getCenterY(), new SlotTree());
                rows.get(s.getCenterY()).addNode(s);
            }
        }
        for (Integer i : rows.keySet()) rows.get(i).constructTree();

        Iterator<Integer> it = rows.keySet().iterator();



        int inputIndex=0;

        //eerst outputjobs uitvoeren tot we een job tegenkomen die nog moet verwerkt worden door de input
        for(Job outputJob:outputJobSequence){
        	Item outputItem=outputJob.getItem();
        	Slot slot=itemToSlot.get(outputItem.getId());

        	//als slot leeg is d.w.z. dat we eerst nog een x-aantal inputjobs moeten afwerken vooraleer we verder kunnen doen moet de outputjobs
        	while(slot==null){
            		Job inputJob=inputJobSequence.get(inputIndex++);

            		//inputjobs verwerken..

                    //InputKraan verplaatsen naar inputslot
                    solution.add(new Move(gantries,0,inputslot.getCenterX(),inputslot.getCenterY(),0));
                    //item oppikken van inputslot
                    gantries.get(0).setItemInCrane(inputJob.getItem());
                    solution.add(new Move(gantries,0,inputslot.getCenterX(),inputslot.getCenterY(),pickupPlaceDuration));
                    //Bestemming nieuw item bepalen
                    if(!it.hasNext()) it = rows.keySet().iterator();
                    Slot leegSlot = rows.get(it.next()).getEmptySlot();

                    //Collision met outputgantry vermijden
                    if(collision(gantries.get(0),gantries.get(1),leegSlot,safetyDistance)){
                        solution.add(new Move(gantries,1,leegSlot.getCenterX()+safetyDistance,gantries.get(1).getyPostion(),0));
                    }

                    //InputKraan verplaatsen naar bestemming
                    solution.add(new Move(gantries,0,leegSlot.getCenterX(),leegSlot.getCenterY(),0));
                    //item neerleggen
                    leegSlot.setItem(gantries.get(0).getItemInCrane());
                    itemToSlot.put(gantries.get(0).getItemInCrane().getId(),leegSlot);
                    gantries.get(0).setItemInCrane(null);
                    solution.add(new Move(gantries,0,leegSlot.getCenterX(),leegSlot.getCenterY(),pickupPlaceDuration));
                    //kijken of het slot ondetussen gevuld is met het outputItem, zoniet opnieuw inputjobs afhandelen
            		slot=itemToSlot.get(outputItem.getId());
        	}

        	//outputjobs verwerken..

            //Overlappende slots van slot met outputitem verplaatsen
            removeOverlappingSlots(slot,rows,solution,it,gantries,itemToSlot,pickupPlaceDuration);

        	//collision met inputKraan vermijden
        	if(collision(gantries.get(1),gantries.get(0),slot,safetyDistance)){
        	    solution.add(new Move(gantries,0,slot.getCenterX()-safetyDistance,gantries.get(0).getyPostion(),0));
            }

            //OutputKraan verplaatsen naar slot met outputitem
            solution.add(new Move(gantries,1,slot.getCenterX(),slot.getCenterY(),0));
            //outputitem oppakken
            gantries.get(1).setItemInCrane(outputItem);
            itemToSlot.remove(outputItem.getId());
            slot.setItem(null);
            solution.add(new Move(gantries,1,slot.getCenterX(),slot.getCenterY(),pickupPlaceDuration));
            //Kraan naar outputslot verplaatsen
            solution.add(new Move(gantries,1,outputslot.getCenterX(),outputslot.getCenterY(),0));
            //item in outputslot neerleggen
            gantries.get(1).setItemInCrane(null);
            solution.add(new Move(gantries,1,outputslot.getCenterX(),outputslot.getCenterY(),pickupPlaceDuration));



        }

        // als alle outputjobs klaar zijn, de rest van de inputjobs verwerken
        for(int i=inputIndex;i<inputJobSequence.size();i++){
        	
        	Job inputJob=inputJobSequence.get(i);

            //inputjobs verwerken..

            //InputKraan verplaatsen naar inputslot
            solution.add(new Move(gantries,0,inputslot.getCenterX(),inputslot.getCenterY(),0));
            //item oppikken van inputslot
            gantries.get(0).setItemInCrane(inputJob.getItem());
            solution.add(new Move(gantries,0,inputslot.getCenterX(),inputslot.getCenterY(),pickupPlaceDuration));
            //Bestemming nieuw item bepalen
            if(!it.hasNext()) it = rows.keySet().iterator();
            Slot leegSlot = rows.get(it.next()).getEmptySlot();

            //Collisioncheck
            if(collision(gantries.get(0),gantries.get(1),leegSlot,safetyDistance)){
                solution.add(new Move(gantries,1,leegSlot.getCenterX()+safetyDistance,gantries.get(1).getyPostion(),0));
            }

            //InputKraan verplaatsen naar bestemming
            solution.add(new Move(gantries,0,leegSlot.getCenterX(),leegSlot.getCenterY(),0));
            //item neerleggen
            leegSlot.setItem(gantries.get(0).getItemInCrane());
            itemToSlot.put(gantries.get(0).getItemInCrane().getId(),leegSlot);
            gantries.get(0).setItemInCrane(null);
            solution.add(new Move(gantries,0,leegSlot.getCenterX(),leegSlot.getCenterY(),pickupPlaceDuration));
        	
        }


        System.out.println("---------Opgelost----------");
        return solution;
    }

    private boolean collision(Gantry van, Gantry tussen, Slot naar, int safetyDistance) {
        if(van.getId() == 0){
            return tussen.getxPosition() < naar.getCenterX()+safetyDistance;
        } else return tussen.getxPosition() > naar.getCenterX()-safetyDistance;
    }

    private void removeOverlappingSlots(Slot slot, Map<Integer, SlotTree> rows, ArrayList<Move> solution, Iterator<Integer> it, List<Gantry> gantries, HashMap<Integer, Slot> itemToSlot, int pickupPlaceDuration) {

        //Alle bovenliggende slots bepalen
        ArrayList<Slot> overlappingSlots = rows.get(slot.getCenterY()).findOverlapping(slot.getXMin(), slot.getXMax(), slot.getZ());
        //System.out.println(overlappingSlots);
        for(Slot s:overlappingSlots){
            //removeOverlappingSlots(s,rows,solution,it,gantries,itemToSlot,pickupPlaceDuration);

            //Dichtste kraan selecteren
            int gantryindex = getClosestGantry(gantries,s);

            //collision
            if(gantryindex == 0){
                //Collision met outputkraan vermijden
                if(collision(gantries.get(0),gantries.get(1),s,safetyDistance)){
                    solution.add(new Move(gantries,1,s.getCenterX()+safetyDistance,gantries.get(1).getyPostion(),0));
                }
            } else{
                //Collision met InputKraan vermijden
                if(collision(gantries.get(1),gantries.get(0),s,safetyDistance)){
                    solution.add(new Move(gantries,0,s.getCenterX()-safetyDistance,gantries.get(0).getyPostion(),0));
                }
            }

            //Kraan verplaatsen naar te verplaatsen item
            solution.add(new Move(gantries,gantryindex,s.getCenterX(),s.getCenterY(),0));
            //item oppakken
            gantries.get(gantryindex).setItemInCrane(s.getItem());
            itemToSlot.remove(s.getItem().getId());
            s.setItem(null);
            solution.add(new Move(gantries,gantryindex,s.getCenterX(),s.getCenterY(),pickupPlaceDuration));
            //Leeg slot kiezen dat niet in dezelfde rij ligt om nieuwe overlapping te voorkomen
            Slot leegSlot;
            do {
                if(!it.hasNext()) it = rows.keySet().iterator();
                leegSlot = rows.get(it.next()).getEmptySlot();
            } while (leegSlot.getCenterY() == slot.getCenterY());

            //collision
            if(gantryindex == 0){
                //Collision met outputkraan vermijden
                if(collision(gantries.get(0),gantries.get(1),leegSlot,safetyDistance)){
                    solution.add(new Move(gantries,1,leegSlot.getCenterX()+safetyDistance,gantries.get(1).getyPostion(),0));
                }
            } else{
                //Collision met inputkraan vermijden
                if(collision(gantries.get(1),gantries.get(0),leegSlot,safetyDistance)){
                    solution.add(new Move(gantries,0,leegSlot.getCenterX()-safetyDistance,gantries.get(0).getyPostion(),0));
                }
            }

            //Kraan verplaatsen naar bestemming
            solution.add(new Move(gantries,gantryindex,leegSlot.getCenterX(),leegSlot.getCenterY(),0));
            //item neerleggen
            leegSlot.setItem(gantries.get(gantryindex).getItemInCrane());
            itemToSlot.put(gantries.get(gantryindex).getItemInCrane().getId(),leegSlot);
            gantries.get(gantryindex).setItemInCrane(null);
            solution.add(new Move(gantries,gantryindex,leegSlot.getCenterX(),leegSlot.getCenterY(),pickupPlaceDuration));
        }


    }

    private int getClosestGantry(List<Gantry> gantries, Slot s) {
        int index = Integer.MIN_VALUE;
        int closestDistenceToSlot = Integer.MAX_VALUE;
        for (Gantry g: gantries) {
            if(Math.abs(g.getxPosition()-s.getCenterX()) < closestDistenceToSlot){
                closestDistenceToSlot = Math.abs(g.getxPosition()-s.getCenterX());
                index = g.getId();
            }
        }
        return index;
    }

    @Override
    public String toString() {
        return "\nminX: " + minX
                + "\nmaxX: " + maxX
                + "\nminY: " + minY
                + "\nmaxY: " + maxY
                + "\nmaxLevels: " + maxLevels
                + "\nsafetyDistance: " + safetyDistance;
    }
}
